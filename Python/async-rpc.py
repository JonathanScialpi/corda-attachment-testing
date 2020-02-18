import asyncio
import aiohttp
import timeit

hashed_files_list = []
recordedRespones = []

#@DEV: Generator used to pass each hash to an asynchronous request
#@PARAM: hashed_files_list is a list of strings that each represent a
#Corda securedHash object.
async def make_files(hashed_files_list):
    for i in hashed_files_list:
        yield i

#@DEV: Gather 8 posts at a time because the default number of event loop threads to be used
#is 2 * 4 (number of cores on the machine) and excute asynchronously.
#@PARAM: hashed_files_list is a list of strings that each represent a
#Corda securedHash object.
async def make_posts(hashed_files_list):
    
    timeout = aiohttp.ClientTimeout(total=None, connect=None,sock_connect=None, sock_read=None)
    connector = aiohttp.TCPConnector(limit=8)
    url = "http://localhost:8999/api/rest/cordapps/workflows/flows/com.template.flows.SendAttachment"
    do_post.start_time = dict()
    async with aiohttp.ClientSession(connector=connector, timeout= timeout) as session:
        post_tasks = []
        # prepare the coroutines that post
        async for hashedFile in make_files(hashed_files_list):
            post_tasks.append(do_post(session, url, hashedFile))
        # now execute them all at once
        await asyncio.gather(*post_tasks)

#DEV: Create a post request who's timer begins at post object's creation and ends once
#a response is received from the server. Append the elapsed time for each request to a list
#called recordedResponses for later data analysis.
#PARAM: aiohttp ClientSession object.
#PARAM: url string of the target proxy server and corDapp REST endpoint.
#PARAM: hashedFile string file that should be sent to the target node.
async def do_post(session, url, hashedFile):
    
    do_post.start_time[hashedFile] = timeit.default_timer()
    async with  session.post(url, json ={
            "receiver": {
                "name": "O=RecipientA, L=Central, C=CA",
                "owningKey": "GfHq2tTVk9z4eXgyPK1JaLiB1tKMNGvJx7s4TT71s2qtEhvnyb9fr24LvcwJ"
            },
            "attachmentHash": hashedFile
        }, timeout=None) as response:
          await response.text()
          elapsed = timeit.default_timer() - do_post.start_time[hashedFile]
          recordedRespones.append(elapsed)

loop = asyncio.get_event_loop()
try:
    startOfLoop = timeit.default_timer()
    loop.run_until_complete(make_posts(hashed_files_list))
    endOfLoop = timeit.default_timer()
    totalElapsedTime = endOfLoop - startOfLoop
    recordedRespones.sort()
    print("Response times: ")
    for record in recordedRespones:
        print(str(record * 1000))
    print("Total execution time: " + str(totalElapsedTime * 1000))
    print("Files tested with: ")
    for hashFile in hashed_files_list:
        print(hashFile)
finally:
    loop.close()