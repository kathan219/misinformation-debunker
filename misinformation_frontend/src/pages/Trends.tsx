import React, { Component, ErrorInfo, ReactNode } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
} from 'recharts';
import { getTrends } from '../lib/api';
import { AlertCircle } from 'lucide-react';
import type { TrendData } from '../types';

// --- 1. ErrorBoundary Component --- //
interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
}

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }
  
  static getDerivedStateFromError(): ErrorBoundaryState {
    return { hasError: true };
  }
  
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
  }
  
  render() {
    if (this.state.hasError) {
      return (
        <div className="rounded-md bg-red-50 p-4">
          <div className="flex">
            <AlertCircle className="h-5 w-5 text-red-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">
                Something went wrong.
              </h3>
            </div>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

// --- 2. Mock Data (for development) --- //
const mockTrends: TrendData[] = [
  {
    topic: "Climate Change",
    frequency: 245,
    sentiment: 0.65,
    period: "2024-03"
  },
  {
    topic: "Public Health",
    frequency: 189,
    sentiment: 0.72,
    period: "2024-03"
  },
  {
    topic: "Elections",
    frequency: 312,
    sentiment: 0.45,
    period: "2024-03"
  },
  {
    topic: "Technology",
    frequency: 156,
    sentiment: 0.58,
    period: "2024-03"
  },
  {
    topic: "Education",
    frequency: 134,
    sentiment: 0.81,
    period: "2024-03"
  }
];

// --- 3. Trends Component --- //
export function Trends() {
  const { data, error, isLoading } = useQuery<TrendData[]>({
    queryKey: ['trends'],
    queryFn: async() => mockTrends,
    initialData: mockTrends, // Using mock data while API is not available
    staleTime: Infinity, // Prevents re-fetching

  });

  // Ensure trends is always an array
  const trends = Array.isArray(data) ? data : [];

  // Debug: log trends data to inspect its shape and content
  console.log("Trends data:", trends);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="animate-pulse text-gray-500">
          Loading trends data...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-md bg-red-50 p-4">
        <div className="flex">
          <AlertCircle className="h-5 w-5 text-red-400" />
          <div className="ml-3">
            <h3 className="text-sm font-medium text-red-800">Error</h3>
            <div className="mt-2 text-sm text-red-700">
              Unable to load trends data. Please try again later.
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <ErrorBoundary>
      <div className="space-y-8">
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight text-gray-900">
            Misinformation Trends
          </h1>
          <p className="mt-4 text-lg text-gray-600">
            Track the most common topics of misinformation and their impact over time.
          </p>
        </div>

        <div className="grid gap-8 md:grid-cols-2">
          <div className="bg-white p-6 rounded-lg shadow-sm">
            <h2 className="text-lg font-semibold mb-4">Topic Frequency</h2>
            <div className="h-[400px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={trends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="topic" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="frequency" fill="#6366f1" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm">
            <h2 className="text-lg font-semibold mb-4">Sentiment Analysis</h2>
            <div className="h-[400px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={trends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="topic" />
                  <YAxis />
                  <Tooltip />
                  <Line
                    type="monotone"
                    dataKey="sentiment"
                    stroke="#6366f1"
                    strokeWidth={2}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm">
          <h2 className="text-lg font-semibold mb-4">Trending Topics</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-300">
              <thead>
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                    Topic
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                    Frequency
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                    Sentiment
                  </th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">
                    Period
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {trends.length > 0 ? (
                  trends.map((trend, index) => (
                    <tr key={index}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {trend.topic}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {trend.frequency}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {trend.sentiment.toFixed(2)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {trend.period}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={4} className="text-center py-4 text-gray-500">
                      No trends data available.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </ErrorBoundary>
  );
}
