<script lang="ts">
  import type { KpiResult } from '@sovity.de/edc-client';
  import { buildEdcClient, type EdcClient } from '@sovity.de/edc-client';

  const edcClient: EdcClient = buildEdcClient({
    managementApiUrl: 'http://localhost:11002/api/management',
    managementApiKey: 'ApiKeyDefaultValue'
  });

  let kpiData: Promise<KpiResult>;

  function refresh() {
    kpiData = edcClient.useCaseApi.getKpis();
  }

  refresh();
</script>

<main class="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8">
  <div class="text-center">
    <h1 class="mt-4 text-3xl font-bold tracking-tight text-gray-900 sm:text-5xl">
      Example TypeScript Client Usage
    </h1>
    <p class="mt-6 text-base leading-7 text-gray-600">
      {#await kpiData}
        Loading...
      {:then data}
        Successfully fetched KPI Endpoint from our EDC API Wrapper Use Case API.
        <table class="mt-6 min-w-full my-table">
          <thead>
            <tr>
              <th scope="col" class="text-left">Field</th>
              <th scope="col" class="text-right">Value</th>
            </tr>
          </thead>
          <tbody>
            {#each Object.entries(data) as [key, value]}
              <tr>
                <td class="text-left whitespace-nowrap">{key}</td>
                <td class="text-right">{JSON.stringify(value)}</td>
              </tr>
            {/each}
          </tbody>
        </table>
      {:catch err}
        Error fetching KPI Endpoint, see console.
      {/await}
    </p>
    <div class="mt-10 flex items-center justify-center gap-x-6">
      <button on:click={refresh} class="btn">Reload</button>
    </div>
  </div>
</main>

<style lang="postcss">
  .my-table {
    @apply divide-y divide-gray-200 table-fixed rounded-lg overflow-hidden;
  }

  .my-table > thead {
    @apply bg-gray-100;
  }

  .my-table > thead > tr > th {
    @apply py-3 px-6 text-xs font-medium tracking-wider text-gray-700 uppercase;
  }

  .my-table > tbody {
    @apply bg-white divide-y divide-gray-200;
  }

  .my-table > tbody > tr > td {
    @apply py-4 px-6 text-sm font-medium text-gray-900;
  }
</style>
