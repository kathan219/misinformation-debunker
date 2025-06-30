import React from 'react';
import { Link } from 'react-router-dom';
import { Search, TrendingUp, Shield } from 'lucide-react';

export function Home() {
  return (
    <div className="space-y-12">
      <div className="text-center">
        <h1 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-6xl">
          Verify Facts, Combat Misinformation
        </h1>
        <p className="mt-6 text-lg leading-8 text-gray-600">
          Our AI-powered fact-checking platform helps you verify claims and stay informed with
          real-time misinformation trends.
        </p>
        <div className="mt-10 flex items-center justify-center gap-x-6">
          <Link
            to="/submit"
            className="rounded-md bg-indigo-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
          >
            Check a Claim
          </Link>
          <Link
            to="/trends"
            className="text-sm font-semibold leading-6 text-gray-900"
          >
            View Trends <span aria-hidden="true">→</span>
          </Link>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="mx-auto max-w-2xl lg:max-w-none">
          <div className="grid grid-cols-1 gap-8 text-center lg:grid-cols-3">
            <div className="bg-white rounded-lg shadow-sm p-8">
              <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-lg bg-indigo-100">
                <Search className="h-6 w-6 text-indigo-600" />
              </div>
              <h3 className="mt-6 text-lg font-semibold text-gray-900">
                Instant Fact-Checking
              </h3>
              <p className="mt-2 text-gray-600">
                Submit any claim and receive instant verification backed by reliable sources.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow-sm p-8">
              <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-lg bg-indigo-100">
                <TrendingUp className="h-6 w-6 text-indigo-600" />
              </div>
              <h3 className="mt-6 text-lg font-semibold text-gray-900">
                Track Trends
              </h3>
              <p className="mt-2 text-gray-600">
                Monitor trending misinformation and stay ahead of viral false claims.
              </p>
            </div>

            <div className="bg-white rounded-lg shadow-sm p-8">
              <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-lg bg-indigo-100">
                <Shield className="h-6 w-6 text-indigo-600" />
              </div>
              <h3 className="mt-6 text-lg font-semibold text-gray-900">
                Reliable Sources
              </h3>
              <p className="mt-2 text-gray-600">
                All fact-checks are supported by credible sources and expert analysis.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}