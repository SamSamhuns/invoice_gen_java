import json
import tqdm
import random


json_src_path = "src/main/resources/common/product/fr/householdandmedia_fr.json"
data_rows = []

with open(json_src_path, 'r') as f:
    prod_list = json.load(f)

filtered_prod_list = []

for i in tqdm.tqdm(range(len(prod_list))):
    item = prod_list[i]
    try:
        if "price" in item:
            del item["price"]
        del item["priceWithTax"]
        del item["priceWithTaxDisplay"]
        del item["priceWithoutTaxDisplay"]
        del item["taxRate"]

        item["price"] = item["priceWithoutTax"]
        del item["priceWithoutTax"]
        filtered_prod_list.append(item)
    except Exception as e:
        print(i)
        print(item)
        print(e)
        break


json_src_path = "householdandmedia_fr.json"
with open(json_src_path, 'w') as f:
    json.dump(filtered_prod_list, f, indent=4, sort_keys=True, ensure_ascii=False)
