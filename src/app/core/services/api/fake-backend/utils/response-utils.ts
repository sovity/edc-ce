export const ok = (body: any): Promise<Response> =>
  new Promise((resolve) => {
    const response = new Response(JSON.stringify(body), {status: 200});
    setTimeout(() => resolve(response), 400);
  });
