export function ok(body: any): Promise<Response> {
  console.log('Fake Backend returns: ', body);
  return new Promise((resolve) => {
    const response = new Response(JSON.stringify(body), {status: 200});
    setTimeout(() => resolve(response), 400);
  });
}
