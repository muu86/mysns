import Image from 'next/image';
import Link from 'next/link';

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="h-screen flex w-full items-center justify-center">
        <div className="max-w-screen-sm w-full flex flex-col gap-y-2 rounded-lg p-4 bg-base-200">
          <h2 className="text-2xl font-bold mb-4">Login with oauth2</h2>
          <Link href="http://localhost:8080/oauth2/authorization/keycloak">
            <button className="btn btn-accent w-full">
              Login with Keycloak
            </button>
          </Link>
        </div>
      </div>
    </main>
  );
}
