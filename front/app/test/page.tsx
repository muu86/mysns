import CreatePostForm from '../ui/post/create-post-form';
import Test from './test';

export default function Post() {
  return (
    <main className="flex min-h-screen flex-col items-center p-24">
      <div className="w-[800px] px-3 rounded-md border-solid border-2 border-gray-400 flex flex-col items-center">
        <div className="py-3 text-center">
          <p className="font-bold">post</p>
        </div>
        <Test />
      </div>
    </main>
  );
}
