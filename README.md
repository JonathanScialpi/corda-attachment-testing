<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

Full Testing Report: https://medium.com/me/stats/post/3670ae670a66

# Corda Attachment Testing - Contents

/Python:
- /async-rpc.py:  Used to make asynchronous REST calls to ``SendAttachment`` flow from a <a href="https://gitlab.com/bluebank/braid/tree/master/braid-server">Braid proxy server</a>. 
- /generate-test-files.py: Simple post request to ``HashedFilesIssue`` flow to create zip files for testing.

/CorDapps:
- ``HashedFilesIssue`` create a set of test files on the node which executes the flow.
- ``SendAttachment`` send a specific test file as an attachment to another node.
