import React, { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { AlertCircle, CheckCircle, XCircle, HelpCircle, ExternalLink } from 'lucide-react';
import { submitFactCheck } from '../lib/api';
import type { FactCheckResult } from '../types';

const VerdictIcon = ({ verdict }: { verdict: FactCheckResult['verdict'] }) => {
  switch (verdict.toLowerCase()) {
    case 'true':
      return <CheckCircle className="h-8 w-8 text-green-500" />;
    case 'false':
      return <XCircle className="h-8 w-8 text-red-500" />;
    case 'misleading':
      return <HelpCircle className="h-8 w-8 text-yellow-500" />;
    default:
      return <AlertCircle className="h-8 w-8 text-gray-500" />;
  }
};

export function Submit() {
  const [claim, setClaim] = useState('');

  const mutation = useMutation<FactCheckResult, Error, string>({
    mutationFn: submitFactCheck,
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (claim.trim()) {
      mutation.mutate(claim);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-8">
      <div className="text-center">
        <h1 className="text-3xl font-bold tracking-tight text-gray-900">
          Submit a Claim for Fact-Checking
        </h1>
        <p className="mt-4 text-lg text-gray-600">
          Enter any claim or statement you'd like to verify. Our AI will analyze it using reliable sources.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label
            htmlFor="claim"
            className="block text-sm font-medium leading-6 text-gray-900"
          >
            Your Claim
          </label>
          <div className="mt-2">
            <textarea
              id="claim"
              rows={4}
              value={claim}
              onChange={(e) => setClaim(e.target.value)}
              className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              placeholder="Enter the claim you want to fact-check..."
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={mutation.isPending}
          className="w-full rounded-md bg-indigo-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50"
        >
          {mutation.isPending ? 'Analyzing...' : 'Check Claim'}
        </button>
      </form>

      {mutation.error && (
        <div className="rounded-md bg-red-50 p-4">
          <div className="flex">
            <AlertCircle className="h-5 w-5 text-red-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">Error</h3>
              <div className="mt-2 text-sm text-red-700">
                Unable to process your request. Please try again later.
              </div>
            </div>
          </div>
        </div>
      )}

      {mutation.data && (
        <div className="bg-white shadow sm:rounded-lg">
          <div className="px-4 py-5 sm:p-6">
            <div className="flex items-center space-x-4">
              <VerdictIcon verdict={mutation.data.verdict} />
              <div>
                <h3 className="text-lg font-medium leading-6 text-gray-900">
                  {mutation.data.verdict.charAt(0).toUpperCase() + mutation.data.verdict.slice(1)}
                </h3>
                <p className="mt-1 text-sm text-gray-500">
                  Confidence Level: {mutation.data.confidence}
                </p>
              </div>
            </div>

            <div className="mt-4 text-sm text-gray-700">
              <h4 className="font-medium">Explanation</h4>
              <p className="mt-1">{mutation.data.explanation}</p>
            </div>

            {mutation.data.sources && mutation.data.sources.length > 0 && (
              <div className="mt-4">
                <h4 className="text-sm font-medium text-gray-700">Sources</h4>
                <ul className="mt-2 space-y-1">
                  {mutation.data.sources.map((source: string, index: number) => {
                    const urlMatch = source.match(/\((https?:\/\/[^\s)]+)\)/);
                    const url = urlMatch ? urlMatch[1] : null;

                    return url ? (
                      <li key={index} className="text-sm">
                        <a
                          href={url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center text-indigo-600 hover:text-indigo-500"
                        >
                          {`Source ${index + 1}`}
                          <ExternalLink className="ml-1 h-4 w-4" />
                        </a>
                      </li>
                    ) : null;
                  })}
                </ul>
              </div>
            )}

            {mutation.data.aiThoughts && mutation.data.aiThoughts.length > 0 && (
              <div className="mt-4">
                <h4 className="text-sm font-medium text-gray-700">AI Thought Process</h4>
                <ul className="mt-2 space-y-1">
                  {mutation.data.aiThoughts.map((thought: string, index: number) => (
                    <li key={index} className="text-sm text-gray-600">
                      {thought}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}