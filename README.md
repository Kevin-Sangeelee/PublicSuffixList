Public Suffix List Helper
-------------------------

##### Terminology used in this page:

* TLD   - Top Level Domain
* SLD   - Second Level Domain
* eTLD  - effective Top Level Domain
* ccSLD - country-code Second Level Domain

The Public Suffix List is a register of domain suffixes that are used in
combination to provide effective Top Level Domains for certain actual Top
Level Domains. It can be found at https://publicsuffix.org/ and on GitHub
https://github.com/publicsuffix/list

For example, the TLD .uk has a number of second-level domains that are for most
purposes considered part of the TLD. Therefore, .co.uk, .ac.uk, .org.uk can all
be thought of as eTLDs, made up of a ccTLD and an actual TLD.

```
	Host    SLD        ccSLD    TLD
	-------------------------------
	www     example             com
	www     example    co       uk
```

Above, the eTLDs would be '.com' and '.co.uk'.

In the `net.susa.cfs.psl` package, I consider the part to the left of the eTLD to
be the SLD. Everything to the left of the SLD is taken as the host.

To achieve this, we load the Public Suffix List into a tree structure. The
first tree level contains the actual TLDs, each of which contains a list of
their ccSLDs, each of which can contain further ccSLDs, and so on.

```
                   root
                    |
         -----------------------
         |                     |
         uk                    au
         |                     |
     ----------         -------------------
     |   |    |         |                 |
    co  ac   org       edu               gov
                        |                 |
                     -------------     -------
                     |     |     |     |     |
                    vic   wa    tas   vic   wa
```

Using a tree allows us to store and search the list of ~9000 entries quickly
when processing large numbers of URLs.

The API to check the host/domain part of a URL is: -
```
    import net.susa.cfs.psl.PublicSuffixList;
    
    // public_suffix_list.dat comes from https://publicsuffix.org/
    String filename = "public_suffix_list.dat";

    PublicSuffixList psl = new PublicSuffixList(filename);
    String etld = psl.getETLD("www.example.co.uk");

    // etld is "co.uk"
```	
Returns the substring that should be considered the TLD. If the domain does not
match any entry from the Public Suffix List, then the first part of the domain
is returned. If the string cannot be parsed as a valid domain, the function
returns the empty string.

#### Building
This repository is stored as an Eclipse project (Oxygen.3a Rel. 4.7.3a), and
includes a POM file if you want to build with Maven. However, there are only
a couple of files and no dependencies, so `javac` and `zip` would easily suffice!
