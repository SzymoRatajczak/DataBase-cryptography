# DataBase-cryptography

The aim of this project is to depict how credentials data in the database  can be protected.
Thread model includes both outside attackers and in-house attackers
Project reflects a typical database crypto infrastructure with breakdowns by: 
*Consumer
*Manager
*Engine
*Documentation
*Crypto provider
*safe


Kernel's packet works like HSM (hardware security module) but unencrpyted data must not be released for outside world
Important rule is: each public method  in kernel before return must zeroed out key in each object in order to remove all key's remains in memeory 
Normally, in real life scenarios  we are using (at least)two databases:
1.the first one  to store our encrypted credentials/bussines data
2. the second one is part of our key safe  and  it stores actual key (if we are using HSM this database is part of HSM otherwise this database will be located on a server )  

I chose to use HSM that's because it's much secure approach than an  application serever

Key safe stores key and provides them to the local machine ( to be more precesily it is a table in a database)
Key's database should be protected e.g by limited access

Thing of big importance is whereabouts of documentation database, it's really urgent matter because  it must be  located in a different place than key's database (for security measures)
The perfect  fit solution is our regular database (with business/credentials data)
