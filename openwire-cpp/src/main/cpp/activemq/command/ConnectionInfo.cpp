/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
#include "activemq/command/ConnectionInfo.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for ConnectionInfo
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
ConnectionInfo::ConnectionInfo()
{
    this->connectionId = NULL ;
    this->clientId = NULL ;
    this->password = NULL ;
    this->userName = NULL ;
    this->brokerPath = NULL ;
    this->brokerMasterConnector = false ;
    this->manageable = false ;
}

ConnectionInfo::~ConnectionInfo()
{
}

unsigned char ConnectionInfo::getDataStructureType()
{
    return ConnectionInfo::TYPE ; 
}

        
p<ConnectionId> ConnectionInfo::getConnectionId()
{
    return connectionId ;
}

void ConnectionInfo::setConnectionId(p<ConnectionId> connectionId)
{
    this->connectionId = connectionId ;
}

        
p<string> ConnectionInfo::getClientId()
{
    return clientId ;
}

void ConnectionInfo::setClientId(p<string> clientId)
{
    this->clientId = clientId ;
}

        
p<string> ConnectionInfo::getPassword()
{
    return password ;
}

void ConnectionInfo::setPassword(p<string> password)
{
    this->password = password ;
}

        
p<string> ConnectionInfo::getUserName()
{
    return userName ;
}

void ConnectionInfo::setUserName(p<string> userName)
{
    this->userName = userName ;
}

        
array<BrokerId> ConnectionInfo::getBrokerPath()
{
    return brokerPath ;
}

void ConnectionInfo::setBrokerPath(array<BrokerId> brokerPath)
{
    this->brokerPath = brokerPath ;
}

        
bool ConnectionInfo::getBrokerMasterConnector()
{
    return brokerMasterConnector ;
}

void ConnectionInfo::setBrokerMasterConnector(bool brokerMasterConnector)
{
    this->brokerMasterConnector = brokerMasterConnector ;
}

        
bool ConnectionInfo::getManageable()
{
    return manageable ;
}

void ConnectionInfo::setManageable(bool manageable)
{
    this->manageable = manageable ;
}

int ConnectionInfo::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseCommand::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalObject(connectionId, mode, ostream) ; 
    size += marshaller->marshalString(clientId, mode, ostream) ; 
    size += marshaller->marshalString(password, mode, ostream) ; 
    size += marshaller->marshalString(userName, mode, ostream) ; 
    size += marshaller->marshalObjectArray(brokerPath, mode, ostream) ; 
    size += marshaller->marshalBoolean(brokerMasterConnector, mode, ostream) ; 
    size += marshaller->marshalBoolean(manageable, mode, ostream) ; 
    return size ;
}

void ConnectionInfo::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseCommand::unmarshal(marshaller, mode, istream) ; 
    connectionId = p_cast<ConnectionId>(marshaller->unmarshalObject(mode, istream)) ; 
    clientId = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    password = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    userName = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    brokerPath = array_cast<BrokerId>(marshaller->unmarshalObjectArray(mode, istream)) ; 
    brokerMasterConnector = (marshaller->unmarshalBoolean(mode, istream)) ; 
    manageable = (marshaller->unmarshalBoolean(mode, istream)) ; 
}
