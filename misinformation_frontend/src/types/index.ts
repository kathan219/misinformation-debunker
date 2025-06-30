export interface FactCheckResult {
  id?: string; 
  claim?: string; 
  verdict: 'True' | 'False' | 'Misleading'; 
  confidence: 'High' | 'Medium' | 'Low'; 
  explanation: string;
  sources: string[];
  sourceSystem: string;
  aiThoughts: string[]; 
  timestamp?: string; 
}

export interface TrendData {
  topic: string;
  frequency: number;
  sentiment: number;
  period: string;
}