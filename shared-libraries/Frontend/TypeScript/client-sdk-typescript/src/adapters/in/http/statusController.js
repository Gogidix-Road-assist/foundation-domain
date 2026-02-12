const { getStatusUseCase } = require('../../application/usecases/getStatusUseCase');

function statusController(_req, res) {
  const status = getStatusUseCase.getStatus();
  res.status(200).json({ status });
}

module.exports = { statusController };
