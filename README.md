<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

#<a href="https://medium.com/corda/experimenting-with-corda-attachments-a95d61d2d372">Experimenting with Corda Attachments</a> 

/Python:
- /async-rpc.py:  Used to make asynchronous REST calls to ``TriggerAttachmentDownload`` flow from a <a href="https://gitlab.com/bluebank/braid/tree/master/braid-server">Braid proxy server</a>. 
- /generate-test-files.py: Simple post request to ``GenerateMockAttachments`` flow to create zip files for testing.

/CorDapps:
- ``GenerateMockAttachments`` create a set of test files on the node which executes the flow.
- ``TriggerAttachmentDownload`` send a specific test file as an attachment to another node.
