import express from 'express';
import cors from 'cors';
import { apiRouter } from './routes/api';

const app = express();
const PORT = 3001;

app.use(cors());
app.use(express.json());
app.use('/api', apiRouter);

app.listen(PORT, () => {
  console.log(`[RayAttributes Sim] 服务已启动: http://localhost:${PORT}`);
});
