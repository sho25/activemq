/*
* Copyright 2006 The Apache Software Foundation or its licensors, as
* applicable.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
#include "command/LocalTransactionId.hpp"

using namespace apache::activemq::client::command;

/*
 *
 *  Marshalling code for Open Wire Format for LocalTransactionId
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
LocalTransactionId::LocalTransactionId()
{
    this->value = NULL ;
    this->connectionId = NULL ;
}

LocalTransactionId::~LocalTransactionId()
{
}

        
long LocalTransactionId::getValue()
{
    return value ;
}

void LocalTransactionId::setValue(long value)
{
    this->value = value ;
}

        
p<ConnectionId> LocalTransactionId::getConnectionId()
{
    return connectionId ;
}

void LocalTransactionId::setConnectionId(p<ConnectionId> connectionId)
{
    this->connectionId = connectionId ;
}
