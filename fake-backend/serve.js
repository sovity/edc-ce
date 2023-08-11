const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const json = (jsonFile) =>
  JSON.parse(fs.readFileSync(path.resolve(jsonFile)).toString());

const app = express();
app.use(
  cors({
    allowedHeaders: [
      'origin',
      'content-type',
      'accept',
      'authorization',
      'x-api-key',
    ],
    credentials: true,
    origin: true,
  }),
);

// Check API Key
app.use((req, res, next) => {
  if (req.header('x-api-key') !== 'no-api-key-required-in-local-dev') {
    res.status(401);
    res.json({message: 'Unauthorized'});
    return;
  }
  next();
});

// Content Type & Status
app.use((req, res, next) => {
  res.header('Content-Type', 'application/json');
  res.status(200);
  next();
});

// Delay Responses
app.use((req, res, next) => setTimeout(next, 1000));

// Management API

const policyDefinitions = json('json/policyDefinitions.json');
app.get('/api/v1/data/policydefinitions', (req, res) => {
  res.json(policyDefinitions);
});

const contractDefinitions = json('json/contractDefinitions.json');
app.get('/api/v1/data/contractdefinitions', (req, res) => {
  res.json(contractDefinitions);
});

const transferProcess = json('json/transferProcess.json');
app.get('/api/v1/data/transferprocess', (req, res) => {
  res.json(transferProcess);
});

const contractAgreements = json('json/contractAgreements.json');
app.get('/api/v1/data/contractagreements', (req, res) => {
  res.json(contractAgreements);
});

const lastCommitInfo = json('json/lastCommitInfo.json');
app.get('/api/v1/data/last-commit-info', (req, res) => {
  res.json(lastCommitInfo);
});

const catalog1 = json('json/catalog1.json');
const catalog2 = json('json/catalog2.json');
app.get('/api/v1/data/catalog', (req, res) => {
  let providerUrl = req.query.providerUrl;
  if (providerUrl === 'http://existing-other-connector/v1/ids/data') {
    res.json(catalog1);
  } else if (providerUrl === 'https://test') {
    res.json(catalog2);
  } else if (providerUrl === 'http://status-502') {
    return res.status(502).json({message: 'Target IDS Endpoint unreachable'});
  } else if (providerUrl === 'http://status-500') {
    return res.status(500).json({message: 'Internal Server Error'});
  } else if (providerUrl === 'http://timeout') {
    return res.status(0);
  } else {
    res.json({contractOffers: []});
  }
});

// Contract Agreement Transfer Extension
app.post(
  '/api/v1/data/contract-agreements-transfer/contractagreements/:contractAgreementId/transfer',
  (req, res) => {
    res.json('{}');
  },
);

// UI API Wrapper
const assetPage = json('json/assets.json');
app.get('/api/v1/data/wrapper/ui/pages/asset-page', (req, res) => {
  res.json(assetPage);
});

const contractAgreementPage = json('json/contractAgreementPage.json');
app.get('/api/v1/data/wrapper/ui/pages/contract-agreement-page', (_, res) => {
  res.json(contractAgreementPage);
});

// Connector Limits
const connectorLimits = json('json/connectorLimits.json');
app.get('/api/v1/data/wrapper/ee/connector-limits', (_, res) => {
  res.json(connectorLimits);
});

// Broker API Wrapper
const brokerCatalogPage = json('json/brokerCatalogPage.json');
app.post('/api/v1/data/wrapper/broker/catalog-page', (_, res) => {
  res.json(brokerCatalogPage);
});
const brokerConnectorPage = json('json/brokerConnectorPage.json');
app.post('/api/v1/data/wrapper/broker/connector-page', (_, res) => {
  res.json(brokerConnectorPage);
});

app.listen(3000, function () {
  console.log('Fake Backend listening on port 3000');
});
