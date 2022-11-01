# Automatic generator of semi-structured documents (SSDs)

Generate semi-structured documents (invoices, payslips, and receipts). This repo is a Java implementation of the two papers mentioned in the acknowledgements.

## Docker (Recommended)

Setup docker in the local system.

```shell
bash scripts/build_docker.sh
bash scripts/run_docker.sh -p EXPOSED_HTTP_PORT
```

The API will be hosted at <http://localhost:EXPOSED_HTTP_PORT/api/ws/> that can be accessed with a default username: `admin` and password: `admin`.

## Local build

### Requirements

-   `Java Development Kit 11/12/13/14/15`
-   `Maven 3.8.6`

Tested on MacOS Big Sur 11.6. The repository should function in linux systems as well. Recommended to install from archived `tar.gz` files available from <https://www.oracle.com/java/technologies/java-se-glance.html> for JDK files and <https://maven.apache.org/download.cgi> for Maven.

Add the following the bash or the current editor profile/rc files.

```shell
# add java paths
export JAVA_HOME="/Users/jdk-15.0.1.jdk/Contents/Home"
export PATH="$PATH:$JAVA_HOME/bin"
# add apache maven paths
export M2_HOME="/Users/apache-maven-3.8.6"
export PATH="$PATH:${M2_HOME}/bin"
```

### Running SSD generation

Generate these documents via the API web interface by launching:

```shell
mvn quarkus:dev
```

The API will be hosted at <http://localhost:9080/api/ws/> that can be accessed with a default username: `admin` and password: `admin`.

### Testing

#### Maven Test

To run all the tests with maven.

```shell
mvn test
```

To run tests for a particular test file inside `src/test/java/com/fairandsmart/generator/documents`:

```shell
mvn test -Dtest=TestAmazonLayout
mvn test -Dntests=2  # only run 2 tests for each layout
```

#### Diversity evaluation (Currently Disabled)

We can evaluate the diversity of the local generated SSD datasets using 4 metrics:

    Alignement, overlapping, SCR_score, and SELF-BLEU :
        TestDiversityLaunch

#### Annotations in Invoices GEDI File

-   Invoice Number - IN
-   Seller(Company) name - SN
-   Seller Address - SA
-   Seller Vat Number/TVA- SVAT
-   Seller Identifier Number(Siren) - SCID
-   Seller Siret - SSIRET
-   Seller TOA/APE - STOA
-   Seller RCS - SRCS
-   Seller Website - SWEB
-   Seller Email - SEMAIL
-   Seller Contact Number - SCN
-   Seller Fax Number - SFAX
-   E-commerce Platform Name(Like Amazon, Ebay,..) - EN
-   E-commerce Platform Website - EWEB
-   E-commerce Order Reference - EOID
    -   Invoice Date - IDATE
    -   Tax Point Date (date of supply) - TPDATE
    -   Billing Name - BN
    -   Billing Address - BA
    -   Billing Contact Number - BCN
    -   Shipping Name - SHN
    -   Shipping Address - SHA
        Shipping Contact Number -SHCN
    -   Table (and its content) -TBL
    -   Client Number - CNUM
    -   Order Number - ONUM
    -   Payment Mode - PMODE
    -   Rest words - undefined

#### Development Support

Generating your own invoices and other SSDs with custom data.

-   Invoice

    -   [Currency, Country, Language](src/main/java/com/fairandsmart/generator/documents/data/generator/GenerationContext.java)
    -   [Country Addresses](src/main/java/com/fairandsmart/generator/documents/data/model/Address.java)
        -   Address CSV files acquired from <https://results.openaddresses.io/>
    -   [Client Info: Bill To, Ship To](src/main/java/com/fairandsmart/generator/documents/data/model/Client.java)
    -   [Company Info](src/main/java/com/fairandsmart/generator/documents/data/model/Company.java)
        -   Required Fields for company info csv file
            -   `name`: Sign or name of the operation
            -   `address_l1`: Address line 1
            -   `address_l2`: Address line 2
            -   `postal_code`
            -   `city` Town/Municipality where the establishment
            -   `country`: Country of the establishment
        -   `companies_ar.csv` and `companies_us.csv` generated from [7-million Company Dataset](https://www.kaggle.com/datasets/peopledatalabssf/free-7-million-company-dataset). Postcodes were randomly generated from the `Faker` library
        -   [The French Companies Sirene Dataset](https://www.sirene.fr/static-resources/doc/dessin_L2_description_complete.pdf?version=1.33.25) csv format requires the same fields as above but named `ENSEIGNE`, `L4_NORMALISEE`, `L5_NORMALISEE`, `CODPOS`, `LIBCOM`, `L7_NORMALISEE`,
    -   [Contact Info: Telephone, Fax](src/main/java/com/fairandsmart/generator/documents/data/model/ContactNumber.java)
        -   The Fields must be present in the Company Info CSV file
    -   [ID Num Info: Tax Num](src/main/java/com/fairandsmart/generator/documents/data/model/IDNumbers.java)
    -   [Invoice Dates](src/main/java/com/fairandsmart/generator/documents/data/model/InvoiceDate.java)
    -   [Invoice Numbers: Client, Invoice Num](src/main/java/com/fairandsmart/generator/documents/data/model/InvoiceNumber.java)
    -   [Logo Generation](src/main/java/com/fairandsmart/generator/documents/data/model/Logo.java)
        -   JSON file
    -   [Product Item Fields](src/main/java/com/fairandsmart/generator/documents/data/model/ProductContainer.java)
    -   1st Signatures dataset from [Kaggle](https://www.kaggle.com/datasets/divyanshrai/handwritten-signatures)
    -   2nd Signatures dataset from [IEEE dataport](https://ieee-dataport.org/open-access/multi-script-handwritten-signature-roman-devanagari)

-   Payslip Specific

    -   [Payment Info](src/main/java/com/fairandsmart/generator/documents/data/model/PaymentInfo.java)
    -   [Payslip Date](src/main/java/com/fairandsmart/generator/documents/data/model/PayslipDate.java)

-   Receipt Specific
    -   [Product Receipt Fields](src/main/java/com/fairandsmart/generator/documents/data/model/ProductReceiptContainer.java)
    -   [Receipt Date](src/main/java/com/fairandsmart/generator/documents/data/model/ReceiptDate.java)

### Acknowledgements

-   Original facogen repo <https://github.com/fairandsmart/facogen>. Re-initialized to remove tracking of large resources.
-   Belhadj, D., Belaïd, Y., & Belaïd, A. (2021, September). Automatic Generation of Semi-structured Documents. In International Conference on Document Analysis and Recognition (pp. 191-205). Springer, Cham.
-   Blanchard, J., Belaïd, Y., & Belaïd, A. (2019, September). Automatic generation of a custom corpora for invoice analysis and recognition. In 2019 International Conference on Document Analysis and Recognition Workshops (ICDARW). IEEE.
-   Xavier Lefevre <xavier.lefevre@fairandsmart.com> / FairAndSmart
-   Nicolas Rueff <nicolas.rueff@fairandsmart.com> / FairAndSmart
-   Alan Balbo <alan.balbo@fairandsmart.com> / FairAndSmart
-   Frederic Pierre <frederic.pierre@fairansmart.com> / FairAndSmart
-   Victor Guillaume <victor.guillaume@fairandsmart.com> / FairAndSmart
-   Jérôme Blanchard <jerome.blanchard@fairandsmart.com> / FairAndSmart
-   Aurore Hubert <aurore.hubert@fairandsmart.com> / FairAndSmart
-   Kevin Meszczynski <kevin.meszczynski@fairandsmart.com> / FairAndSmart
