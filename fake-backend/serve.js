const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
app.use(cors());

function json(jsonFile) {
  return JSON.parse(fs.readFileSync(path.resolve(jsonFile)).toString());
}

const assets = json('json/assets.json');
const policyDefinitions = json('json/policyDefinitions.json');
const contractDefinitions = json('json/contractDefinitions.json');
const transferProcess = json('json/transferProcess.json');
const contractAgreements = json('json/contractAgreements.json');
const catalog1 = json('json/catalog1.json');
const catalog2 = json('json/catalog2.json');

app.use((req, res, next) => {
  res.header('Content-Type', 'application/json');
  res.status(200);
  next();
});

// Delay Responses
app.use((req, res, next) => setTimeout(next, 1000));

app.get('/api/v1/data/assets', (req, res) => {
  res.json(assets);
});

app.get('/api/v1/data/policydefinitions', (req, res) => {
  res.json(policyDefinitions);
});

app.get('/api/v1/data/contractdefinitions', (req, res) => {
  res.json(contractDefinitions);
});

app.get('/api/v1/data/transferprocess', (req, res) => {
  res.json(transferProcess);
});

app.get('/api/v1/data/contractagreements', (req, res) => {
  res.json(contractAgreements);
});

app.get('/api/v1/data/catalog', (req, res) => {
  let providerUrl = req.query.providerUrl;
  if (providerUrl === 'http://existing-other-connector/v1/ids/data') {
    res.json(catalog1);
  } else if (providerUrl === 'https://test.gg') {
    res.json(catalog2);
  } else {
    res.json({contractOffers: []});
  }
});

app.listen(3000, function () {
  console.log('Fake Backend listening on port 3000');
});
