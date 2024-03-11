import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import { NodejsFunction } from 'aws-cdk-lib/aws-lambda-nodejs';
import * as apigw from 'aws-cdk-lib/aws-apigateway';

export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const bucket = new s3.Bucket(this, 'MySns', {
      bucketName: 'my-sns',
      versioned: true,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    const api = new apigw.RestApi(this, 'MySnsApi', {
      restApiName: 'my-sns-api',
    });

    const files = api.root.addResource('files');
    const file = files.addResource('{id}');

    const layer = new lambda.LayerVersion(this, 'ImageProcessingLayer', {
      code: lambda.Code.fromAsset(
        'resources/lambda-layer/image-processing-layer'
      ),
      compatibleRuntimes: [lambda.Runtime.NODEJS_20_X],
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    const fileHandler = new NodejsFunction(this, 'file-handler', {
      functionName: 'file-handler-v2',
      runtime: lambda.Runtime.NODEJS_20_X,
      entry: './resources/lambda/file-handler.ts',
      handler: 'handler',
      layers: [layer],
      bundling: {
        externalModules: ['sharp'],
      },
    });

    files.addMethod('POST', new apigw.LambdaIntegration(fileHandler));
  }
}
