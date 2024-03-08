https://nextjs.org/learn/dashboard-app/fetching-data

However... there are two things you need to be aware of:

- The data requests are unintentionally blocking each other, creating a request waterfall.
- By default, Next.js prerenders routes to improve performance, this is called Static Rendering. So if your data changes, it won't be reflected in your dashboard.

### Request Waterfall

일련의 Request들이 순차적으로 호출되는 형태.

```ts
const revenue = await fetchRevenue();
const latestInvoices = await fetchLatestInvoices(); // wait for fetchRevenue() to finish
const {
  numberOfInvoices,
  numberOfCustomers,
  totalPaidInvoices,
  totalPendingInvoices,
} = await fetchCardData(); // wait for fetchLatestInvoices() to finish
```

#### request waterfall 이 유용한 경우

이전의 request가 만족되어야 다음 request를 날릴 수 있는 경우에는 유용할 수 있다. response에서 받아온 데이터를 다음 request에 파라미터를 넣는 경우.

### Parallel data fetching

request waterfall을 피하기 위해서 모든 request를 한번에 날릴 수 있다.  
순수 자바스크립트 API를 사용할 수 있다.
**Promise.all()**, **Promise.allSettled()**

```ts
export async function fetchCardData() {
  try {
    const invoiceCountPromise = sql`SELECT COUNT(*) FROM invoices`;
    const customerCountPromise = sql`SELECT COUNT(*) FROM customers`;
    const invoiceStatusPromise = sql`SELECT
         SUM(CASE WHEN status = 'paid' THEN amount ELSE 0 END) AS "paid",
         SUM(CASE WHEN status = 'pending' THEN amount ELSE 0 END) AS "pending"
         FROM invoices`;

    const data = await Promise.all([
      invoiceCountPromise,
      customerCountPromise,
      invoiceStatusPromise,
    ]);
    // ...
  }
}
```

### Static and Dynamic Rendering

#### Static Rendering

빌드 타임에 서버에서 렌더링하는 것.

- Prerendered 된 컨텐트를 캐쉬할 수 있기 때문에 웹사이트가 빨라진다.
- 컨텐트가 캐쉬되기 때문에 서버에 부하를 줄인다.
- SEO: 검색엔진 최적화에 유리하다.

그런데 하나의 요청이 다른 요청보다 느려서 전체 Promise가 delay되는 경우는 어떻게 해결할 수 있는가.

#### Dynamic Rendering

각 유저의 요청 시마다 request time에 렌더링하는 것.

- 실시간 데이터: 렌더링된 결과물이 실시간 정보를 반영한다. 데이터가 자주 변하는 앱에 적합하다.
- 유저 맞춤형 컨텐트를 보여줄 수 있음.
- request time에만 알 수 있는 정보를 렌더링 시에 활용가능.(cookies, url parameter...)

### Streaming

전송해야할 데이터를 **chunk**단위로 나누고 점진적으로 서버에서 클라이언트로 streaming 하는 것.

streaming을 활용하여 전체 요청 중 하나의 요청이 느려져서 전체 요청이 처리되는 것을 막는 경우를 방지할 수 있다. 사용자는 모든 요청이 완료되기 전에도 웹 페이지와 상호작용 할 수 있다.

Nextjs가 streaming을 구현하는 방법

- page 레벨에서 loading.tsx 활용
- 컴포넌트 레벨에서 <Suspense> 컴포넌트 활용

```ts
export default async function Page() {
  // ...

        <Suspense fallback={<RevenueChartSkeleton />}>
          <RevenueChart />
        </Suspense>
        <Suspense fallback={<LatestInvoicesSkeleton />}>
          <LatestInvoices />
        </Suspense>
  // ...
}
```

#### Suspense Boundaries 설정 시 생각해야 할 것

- loading.tsx로 전체 페이지를 스트리밍할 수 있으나 가장 느리게 처리되는 하나의 요청때문에 전체 요청이 블로킹되는 단점이 있다.
- 그렇다고 각각의 모든 컴포넌트에 Suspense를 입히게 되면 컴포넌트 하나가 렌더가 완료될 때마다 브라우저에서 popping한다.
- 애플리케이션 마다 사용자의 경험을 고려하여 Suspense Boundaries를 설정해야 한다.
