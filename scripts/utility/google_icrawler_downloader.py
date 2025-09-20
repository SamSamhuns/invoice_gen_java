import csv
import tqdm
from icrawler.builtin import GoogleImageCrawler


csv_path = "companies_ae_en.csv"

with open(csv_path, newline="") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=";")
    header = next(csv_reader)

    for row in tqdm.tqdm(csv_reader):
        name, domain, industry, country, address1, address2, postcode = row

        dest_dir = f"logos/{name.replace('/', ' ')}"
        keyword = f"{name} logo"
        google_crawler = GoogleImageCrawler(
            feeder_threads=4,
            parser_threads=8,
            downloader_threads=8,
            storage={"root_dir": dest_dir},
        )

        google_crawler.crawl(keyword=keyword, max_num=5, file_idx_offset=0)
