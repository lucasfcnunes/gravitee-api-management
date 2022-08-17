/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io - Management API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import {
    Selector,
    SelectorFromJSON,
    SelectorFromJSONTyped,
    SelectorToJSON,
    SelectorHttpV4AllOf,
    SelectorHttpV4AllOfFromJSON,
    SelectorHttpV4AllOfFromJSONTyped,
    SelectorHttpV4AllOfToJSON,
} from './';

/**
 * 
 * @export
 * @interface SelectorHttpV4
 */
export interface SelectorHttpV4 extends Selector {
    /**
     * 
     * @type {string}
     * @memberof SelectorHttpV4
     */
    path: string;
    /**
     * 
     * @type {string}
     * @memberof SelectorHttpV4
     */
    pathOperator: SelectorHttpV4PathOperatorEnum;
    /**
     * 
     * @type {Array<string>}
     * @memberof SelectorHttpV4
     */
    methods?: Array<SelectorHttpV4MethodsEnum>;
}

export function SelectorHttpV4FromJSON(json: any): SelectorHttpV4 {
    return SelectorHttpV4FromJSONTyped(json, false);
}

export function SelectorHttpV4FromJSONTyped(json: any, ignoreDiscriminator: boolean): SelectorHttpV4 {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...SelectorFromJSONTyped(json, ignoreDiscriminator),
        'path': json['path'],
        'pathOperator': json['pathOperator'],
        'methods': !exists(json, 'methods') ? undefined : json['methods'],
    };
}

export function SelectorHttpV4ToJSON(value?: SelectorHttpV4 | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...SelectorToJSON(value),
        'path': value.path,
        'pathOperator': value.pathOperator,
        'methods': value.methods,
    };
}

/**
* @export
* @enum {string}
*/
export enum SelectorHttpV4PathOperatorEnum {
    STARTSWITH = 'STARTS_WITH',
    EQUALS = 'EQUALS'
}
/**
* @export
* @enum {string}
*/
export enum SelectorHttpV4MethodsEnum {
    CONNECT = 'CONNECT',
    DELETE = 'DELETE',
    GET = 'GET',
    HEAD = 'HEAD',
    OPTIONS = 'OPTIONS',
    PATCH = 'PATCH',
    POST = 'POST',
    PUT = 'PUT',
    TRACE = 'TRACE',
    OTHER = 'OTHER'
}


