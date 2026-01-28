from flask import Flask, request, jsonify
import joblib
import pandas as pd

# 1. initialize the app
app = Flask(__name__)

# 2. Load the "Brain" and the "memory"
# We lod the model and the exact list of columns it was trained on
model = joblib.load('penguin_brain.pkl')
model_columns = joblib.load('penguin_columns.pkl')

print("Model and Columns loaded successfully")

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # 3. Get the JSON data sent by the user:
        json_data = request.get_json()

        # 4. Convert JSON to a DataFrame (1 row)
        query_df = pd.DataFrame([json_data])

        # 5. The "Dummy" problem
        # The model expects 'island_Dream', 'sex_Male', etc.
        # But we only got raw text like "Island": "Dream"
        # We process it exactly like we did in training:
        query_encoded = pd.get_dummies(query_df, columns=['island', 'sex'])

        # 6. The 'Alignment' Trick (CRITICAL STEP)
        # pd.get_dummies might miss columns (e.g., if input is "Dream", it won't create 'island_Torgersen').
        # Any missing column gets filled with 0.
        query_final = query_encoded.reindex(columns=model_columns, fill_value=0)

        # 7. Ask the Brain
        prediction = model.predict(query_final)

        # 8. Send the answer back
        return jsonify({'species': prediction[0]})
    except Exception as e:
        return jsonify({'error': str(e)})

if __name__ == '__main__':
    app.run(port=5000, debug=True)