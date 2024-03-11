export default function Login() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="w-72 rounded-md border-solid border-2 border-gray-400 flex-col items-center justify-center">
        <div className="py-3 text-center">
          <p className="font-bold">login</p>
        </div>
        <div className="py-3 text-center">
          <a href="http://localhost:8080/oauth2/authorization/keycloak">
            keycloak
          </a>
        </div>
      </div>
    </main>
  );
}
