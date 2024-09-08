from urllib.request import urlopen
import json

url = ""
apk_url = url.replace("index.min.json", "apk")
repo_apps = json.loads(urlopen(url).read())

apk_links = [ f"{apk_url}/{entry['apk']}" for entry in repo_apps ]

for i in apk_links:
    print(i)