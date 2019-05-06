import requests
from bs4 import BeautifulSoup

import re
import time
import os
import mimetypes
import sys

DOWNLOAD_DIR = "/sdcard/Home/saved_html"
ID_PREFIX = time.strftime("%Y%m%d_")

existed_files = os.listdir(DOWNLOAD_DIR)
INDEX = sum([i.startswith(ID_PREFIX) for i in existed_files])
HTML_NAME = ID_PREFIX + str(INDEX)

post_url = sys.argv[1]

response = requests.get(post_url)
text = response.text

re_pattern_dict = {
    "publish_time" : 'publish_time = "(\d{4}-\d{2}-\d{2})"',
    "js_view_source" : "msg_source_url = '(.*)'"
}
re_result_dict = {k:re.findall(v, text)[0] for k,v in re_pattern_dict.items()}

soup = BeautifulSoup(text, "html.parser")

soup.find(id = "publish_time").string = re_result_dict["publish_time"]
source = re_result_dict["js_view_source"]
if source:
    source_article = soup.find(id = "js_view_source")
    source_article.attrs['href'] = source

# remove unnecessary <script>
for i in soup.find_all("script"):
    i.decompose()

divs = soup.body.find_all("div", recursive=False)
for i in divs:
    if i.attrs['id'] != 'js_article':
        i.decompose()

imgs = soup.find_all("img")
if len(imgs) != 0:
    os.makedirs(os.path.join(DOWNLOAD_DIR,"statics",HTML_NAME))
    n = 0
    for i in imgs:
        datasrc = i.attrs.get("data-src")
        if datasrc:
            #print(datasrc)
            pic = requests.get(datasrc)
            pic_type = mimetypes.guess_extension(pic.headers['content-type'])
            pic_name = "pic%s"%n + pic_type
            with open(os.path.join(DOWNLOAD_DIR, "statics", HTML_NAME, pic_name), "wb") as f:
                f.write(pic.content)
            i.attrs['src'] = os.path.join("statics", HTML_NAME, pic_name)
            i.attrs.pop("data-src")
        n += 1

with open(os.path.join(DOWNLOAD_DIR, HTML_NAME+".html") ,"w") as f:
    f.write(str(soup))

