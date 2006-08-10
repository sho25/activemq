/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "activemq/command/BrokerInfo.hpp"

using namespace apache::activemq::command;

/*
 *
 *  Command and marshalling code for OpenWire format for BrokerInfo
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
BrokerInfo::BrokerInfo()
{
    this->brokerId = NULL ;
    this->brokerURL = NULL ;
    this->peerBrokerInfos = NULL ;
    this->brokerName = NULL ;
    this->slaveBroker = false ;
    this->masterBroker = false ;
    this->faultTolerantConfiguration = false ;
}

BrokerInfo::~BrokerInfo()
{
}

unsigned char BrokerInfo::getDataStructureType()
{
    return BrokerInfo::TYPE ; 
}

        
p<BrokerId> BrokerInfo::getBrokerId()
{
    return brokerId ;
}

void BrokerInfo::setBrokerId(p<BrokerId> brokerId)
{
    this->brokerId = brokerId ;
}

        
p<string> BrokerInfo::getBrokerURL()
{
    return brokerURL ;
}

void BrokerInfo::setBrokerURL(p<string> brokerURL)
{
    this->brokerURL = brokerURL ;
}

        
array<BrokerInfo> BrokerInfo::getPeerBrokerInfos()
{
    return peerBrokerInfos ;
}

void BrokerInfo::setPeerBrokerInfos(array<BrokerInfo> peerBrokerInfos)
{
    this->peerBrokerInfos = peerBrokerInfos ;
}

        
p<string> BrokerInfo::getBrokerName()
{
    return brokerName ;
}

void BrokerInfo::setBrokerName(p<string> brokerName)
{
    this->brokerName = brokerName ;
}

        
bool BrokerInfo::getSlaveBroker()
{
    return slaveBroker ;
}

void BrokerInfo::setSlaveBroker(bool slaveBroker)
{
    this->slaveBroker = slaveBroker ;
}

        
bool BrokerInfo::getMasterBroker()
{
    return masterBroker ;
}

void BrokerInfo::setMasterBroker(bool masterBroker)
{
    this->masterBroker = masterBroker ;
}

        
bool BrokerInfo::getFaultTolerantConfiguration()
{
    return faultTolerantConfiguration ;
}

void BrokerInfo::setFaultTolerantConfiguration(bool faultTolerantConfiguration)
{
    this->faultTolerantConfiguration = faultTolerantConfiguration ;
}

int BrokerInfo::marshal(p<IMarshaller> marshaller, int mode, p<IOutputStream> ostream) throw (IOException)
{
    int size = 0 ;

    size += BaseCommand::marshal(marshaller, mode, ostream) ; 
    size += marshaller->marshalObject(brokerId, mode, ostream) ; 
    size += marshaller->marshalString(brokerURL, mode, ostream) ; 
    size += marshaller->marshalObjectArray(peerBrokerInfos, mode, ostream) ; 
    size += marshaller->marshalString(brokerName, mode, ostream) ; 
    size += marshaller->marshalBoolean(slaveBroker, mode, ostream) ; 
    size += marshaller->marshalBoolean(masterBroker, mode, ostream) ; 
    size += marshaller->marshalBoolean(faultTolerantConfiguration, mode, ostream) ; 
    return size ;
}

void BrokerInfo::unmarshal(p<IMarshaller> marshaller, int mode, p<IInputStream> istream) throw (IOException)
{
    BaseCommand::unmarshal(marshaller, mode, istream) ; 
    brokerId = p_cast<BrokerId>(marshaller->unmarshalObject(mode, istream)) ; 
    brokerURL = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    peerBrokerInfos = array_cast<BrokerInfo>(marshaller->unmarshalObjectArray(mode, istream)) ; 
    brokerName = p_cast<string>(marshaller->unmarshalString(mode, istream)) ; 
    slaveBroker = (marshaller->unmarshalBoolean(mode, istream)) ; 
    masterBroker = (marshaller->unmarshalBoolean(mode, istream)) ; 
    faultTolerantConfiguration = (marshaller->unmarshalBoolean(mode, istream)) ; 
}
