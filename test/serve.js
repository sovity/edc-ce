const apimock = require('@ng-apimock/core');
const express = require('express');
const fs = require("fs");
const path = require("path");
const app = express();
app.set('port', 3000);

const assets = JSON.parse(fs.readFileSync(path.resolve('json/assets.json')).toString())
const policyDefinitions = JSON.parse(fs.readFileSync(path.resolve('json/policyDefinitions.json')).toString())
const contractDefinitions = JSON.parse(fs.readFileSync(path.resolve('json/contractDefinitions.json')).toString())
const transferProcess = JSON.parse(fs.readFileSync(path.resolve('json/transferProcess.json')).toString())
const contractAgreements = JSON.parse(fs.readFileSync(path.resolve('json/contractAgreements.json')).toString())

apimock.processor.process({
  src: 'mocks'
});

app.use(apimock.middleware);

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Headers', '*')
  res.header('Access-Control-Allow-Methods', '*')
  res.header('Content-Type', 'application/json')
  res.status(200)
  next()
})


app.listen(app.get('port'), () => {
  console.log('@ng-apimock/core running on port', app.get('port'));
});

app.get('/assets', (req, res) => {
  res.json(assets)
})

app.get('/policydefinitions', (req, res) => {
  res.json(policyDefinitions)
})

app.get('/contractdefinitions', (req, res) => {
  res.json(contractDefinitions)
})

app.get('/transferProcess', (req, res) => {
  res.json(transferProcess)
})

app.get('/contractagreements', (req, res) => {
  res.json(contractAgreements)
})

