/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/

#ifndef ActiveMQTempTopic_h_
#define ActiveMQTempTopic_h_

#include <string>
#include <vector>
#include <exception>
#include <inttypes.h>
#include <boost/shared_ptr.hpp>

#include "command/ActiveMQTempDestination.h"    

namespace ActiveMQ {
  namespace Command {

    /*
     *
     *  Marshalling code for Open Wire Format for ActiveMQTempTopic
     *
     *
     *  NOTE!: This file is autogenerated - do not modify!
     *         if you need to make a change, please see the Groovy scripts in the
     *         activemq-core module
     *
     */
    class ActiveMQTempTopic : public ActiveMQTempDestination
    {
      private:

      public:
        const static int TYPE = 103;
    
      public:
        ActiveMQTempTopic();
        virtual ~ActiveMQTempTopic();
    
        virtual bool isMarshalAware() const;
        virtual int getCommandType() const;
    };
  }
}

#endif /*ActiveMQTempTopic_h_*/
