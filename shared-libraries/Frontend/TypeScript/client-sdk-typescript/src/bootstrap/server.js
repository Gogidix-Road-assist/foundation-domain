const express = require('express');
const pinoHttp = require('pino-http');

const { registerRoutes } = require('../adapters/in/http/routes');

function createApp() {
  const app = express();

  app.use(express.json());
  app.use(pinoHttp());

  registerRoutes(app);

  return app;
}

async function main() {
  const port = process.env.PORT ? Number(process.env.PORT) : 3000;
  const app = createApp();

  app.listen(port, () => {
    // eslint-disable-next-line no-console
    console.log(`client-sdk-typescript listening on http://localhost:${port}`);
  });
}

if (require.main === module) {
  main().catch((err) => {
    // eslint-disable-next-line no-console
    console.error(err);
    process.exit(1);
  });
}

module.exports = { createApp };
