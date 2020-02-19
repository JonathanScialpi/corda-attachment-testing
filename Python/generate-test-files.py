import json
import requests

#@DEV: create X amount of files of Y size with some dummy byte like 0.
#@PARAM: url string REST endpoint that calls the HashedFilesIssue flow on target node.
#@PARAM: test_name string name for your test.
#@PARAM: content byte is the dummy char that will be repeated in the file to reach the file_size.
#@PARAM: file_size int how big each file should be in megabytes.
#@PARAM: number_of_files int the quantity of the files to generate on the node.
def generate_hashed_files(url, test_name, content, file_size, number_of_files, loops):
    for i in range(loops):         
        response = requests.post(url, json={
            "testName": test_name + str(i) + "-",
            "content": content,
            "fileSize": file_size,
            "numberOfFiles": number_of_files
        })

        print(json.loads(response.text)["tx"]["outputs"][0]["data"]["fileHashes"])

url = "http://localhost:8999/api/rest/cordapps/workflows/flows/com.template.flows.HashedFilesIssue"
generate_hashed_files(url, "useast-100-10mb-01-", 0, 10, 10, 10)
