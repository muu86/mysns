import { APIGatewayProxyEvent } from 'aws-lambda';
import sharp from 'sharp';

export const handler = async (event: APIGatewayProxyEvent) => {
  console.log(sharp);
  try {
    // fetch is available with Node.js 18
    // const res = await fetch(url);
    return {
      statusCode: 200,
      body: JSON.stringify({
        message: event,
        nodepath: process.env.NODE_PATH,
      }),
    };
  } catch (err) {
    console.log(err);
    return {
      statusCode: 500,
      body: JSON.stringify({
        message: 'some error happened',
      }),
    };
  }
};
