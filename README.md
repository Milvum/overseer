# [StemApp](https://milvum.github.io/stemapp/)

At the end of 2016 we started our journey with the goal: making digital voting possible for The Netherlands. In co-creation with five municipalities, we worked together to enable citizen participation on the blockchain. We believe this is a perfect step for making digital voting on the blockchain a reality. We have worked hard to realize the project and are pleased that a first version is now available for further development. That is why we are now doing an Open Source release of our StemApp project. We invite developers and security researchers to help The Netherlands in the next phase of digital voting. We be

Please mention [Milvum](https://milvum.com) in communications when using this code.

# Overseer 

An android app, used as a temporary solution for handing out Voting Passes by scanning the QR-Codes in of StemApp users. 

Requests of voting passes occur through a `/beg` request to the [ballot-dispenser](https://github.com/Milvum/ballot-dispenser). You can the Overseer to the correct address in the `app/build.gradle` file.

## Disclaimer

The project in the current state is not market ready and thus should only be used for pilots or testing. In its current state the StemApp is not yet fully tested and not entirely secure (see open issues in the [whitepaper](https://milvum.com/en/download-stemapp-whitepaper/)). This version is also not yet ready for a release on the public Ethereum network. Milvum is not accountable for the use of the StemApp in any way, and the possible outcomes this may have.
