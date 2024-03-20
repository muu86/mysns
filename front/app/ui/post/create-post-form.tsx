'use client';

import { createPost } from '@/app/lib/actions/post';
import { PlusIcon } from '@heroicons/react/24/outline';
import { ChangeEvent, useState } from 'react';
import Preview from './preview-image';

type ImageFile = {
  url: string;
  file: File;
};

export default function CreatePostForm() {
  const [content, setContent] = useState<string>('');
  const [imageFiles, setImageFiles] = useState<ImageFile[]>([]);
  const [selectedFileUrl, setSelectedFileUrl] = useState<string>('');

  const handleFileInput = (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    const file = event.target.files?.[0];
    if (file) {
      const url = URL.createObjectURL(file);
      setImageFiles([...imageFiles, { url, file }]);
      setSelectedFileUrl(url);
    }
  };

  const handleClearPreview = (id: string) => {
    // 배열 중에 하나를 삭제했을 때 selectedImageId 값을 다시 설정해줘야 함.
    // 삭제한 이미지의 왼쪽 이미지를 자동으로 선택하도록 한다.
    // 원래 index 저장

    // 리스트 길이가 1 이라 삭제하면 선택된 이미지가 없는 경우
    // 리스트 길이는 1 이상인데 0번 인덱스(첫번째 이미지)를 삭제하는 경우
    // 리스트 길이도 1 이상이고 1번 인덱스를 삭제하는 경우가 아니라 그냥 인덱스 - 1 만 하면 되는 경우
    const indexToDelete = imageFiles.findIndex((f) => f.url === id);
    if (selectedFileUrl === imageFiles[indexToDelete].url) {
      let nextSelectedIndex =
        indexToDelete === 0 ? indexToDelete + 1 : indexToDelete - 1;
      if (imageFiles.length === 1) nextSelectedIndex = -1;
      setSelectedFileUrl(
        nextSelectedIndex === -1 ? '' : imageFiles[nextSelectedIndex].url
      );
    }

    setImageFiles(
      imageFiles.filter((file) => {
        return file.url !== id;
      })
    );
  };

  // const createPost = () => {
  //   console.log(arguments);
  //   const fd = new FormData();
  //   imageFiles.forEach((f, i) => {
  //     fd.append('files', f.file);
  //   });
  //   return createPostDto.bind(null, fd);
  // })();

  const preCreatePost = (formData: FormData) => {
    const fd = new FormData();
    fd.append('content', content);
    imageFiles.forEach((f, i) => {
      fd.append(`files`, f.file);
    });
    // fd.append('files', formData.getAll('files'));

    createPost(fd);
  };

  return (
    <div className="flex flex-col w-full py-4">
      <form action={preCreatePost}>
        {/* <form
        action="http://localhost:8080/posts"
        encType="multipart/form-data"
        method="post"
        > */}
        {/* 이미지, 미디어 파일 */}
        <div>
          {imageFiles && (
            <Preview
              imageFiles={imageFiles}
              handleClearPreview={handleClearPreview}
              selectedFileUrl={selectedFileUrl}
              setSelectedFileUrl={setSelectedFileUrl}
            />
          )}
          <div className="py-2 text-sm border-2 border-dashed hover:border-neutral-900 transition duration-150 hover:ease-in ease-out">
            <label className="w-full h-full flex flex-col justify-center items-center hover:cursor-pointer">
              <PlusIcon className="w-10 h-10" />
              <input
                className="hidden"
                type="file"
                accept="image/**"
                multiple
                name="files"
                // name="files"
                onChange={handleFileInput}
              />
              이미지 추가
            </label>
          </div>
        </div>
        {/* 텍스트 */}
        <div className="py-3">
          <div className="border-2 border-dashed hover:border-neutral-900 transition duration-150 hover:ease-in ease-out has-[:focus]:border-neutral-800 has-[:focus]:border-solid">
            <textarea
              name="content"
              className="w-full min-h-56 p-6 resize-none focus:outline-none"
              onChange={(e) => setContent(e.target.value)}
            ></textarea>
          </div>
        </div>
        {/* 저장 */}
        <div className="w-full">
          <button
            type="submit"
            className="w-1/3 mx-auto p-5 rounded-lg text-center bg-neutral-900 text-white"
            // onClick={post}
          >
            {/* <ArrowUpTrayIcon className="w-" /> */}
            저장
          </button>
        </div>
      </form>
    </div>
  );
}
