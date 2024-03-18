import { getUser } from '@/app/lib/actions';

export default async function User() {
  const userData = await getUser();
  // console.log(userData);
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      {/* <div className="w-72 px-3 rounded-md border-solid border-2 border-gray-400 flex-col items-center justify-center">
        <div className="py-3 text-center">
          <p className="font-bold">user</p>
        </div>
        <div className="py-3 text-center break-words">
          {userData?.err
            ? userData.message
            : `token: ${userData.principal.tokenValue}`}
        </div>
      </div> */}
    </main>
  );
}
