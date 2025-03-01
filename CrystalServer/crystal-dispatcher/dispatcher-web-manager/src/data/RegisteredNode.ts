import type {OllamaModel} from "@/data/OllamaModel.ts";
import type {DeepSeekModel} from "@/data/DeepSeekModel.ts";

export interface RegisteredNode {
    nodeId: string,
    nodeName: string,
    host: string,
    port: number,
    ssl: boolean,
    isAlive: boolean,
    registeredTimestamp: number,
    lastAliveTimestamp: number,
    lastAliveCheckTimestamp: number,
    lastUpdateTimestamp: number,
    ollamaModels: OllamaModel[],
    deepseekModels: DeepSeekModel[],
    requestUrl: string,
    nettyClientPort: number | null,
    isNettyClientConnected: boolean
}