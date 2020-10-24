### Instructions

#### Pre-Requirements
- Have Docker installed
- Be on the root folder of this project Eg.: `<PATH_TO_CHALLENGE>/llc`

#### Build DB Image
```shell script
cd challenge-llc/misc/database
docker image build -t llc-db .
```

#### Build Application
```shell script
cd ../../../
mvn clean install

cd challenge-llc
docker image build -t llc-server .
```

#### Run Application
```shell script
docker-compose up
```

#### Run Distribution
```shell script
curl -d '{ "payout": 100.0 }' -H "Content-Type: application/json" -X POST http://localhost:8885/llc/v1/distribution/payout \
&& curl -d '{ "payout": 200.0 }' -H "Content-Type: application/json" -X POST http://localhost:8885/llc/v1/distribution/payout \
&& curl -d '{ "payout": 300.0 }' -H "Content-Type: application/json" -X POST http://localhost:8885/llc/v1/distribution/payout \
&& curl -d '{ "payout": 400.0 }' -H "Content-Type: application/json" -X POST http://localhost:8885/llc/v1/distribution/payout
```