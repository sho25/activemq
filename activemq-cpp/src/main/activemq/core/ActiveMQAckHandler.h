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
#ifndef _ACTIVEMQ_CORE_ACTIVEMQACKHANDLER_H_
#define _ACTIVEMQ_CORE_ACTIVEMQACKHANDLER_H_

#include <cms/CMSException.h>

namespace activemq{
namespace core{

    class ActiveMQMessage;

    /**
     * Interface class that is used to give CMS Messages an interface to
     * Ack themselves with.
     */
    class ActiveMQAckHandler
    {
    public:
    
    	virtual ~ActiveMQAckHandler(void) {};
    
        /**
         * Method called to acknowledge the message passed
         * @param Message to Acknowlegde
         * @throw CMSException
         */
        virtual void acknowledgeMessage( const ActiveMQMessage* message )
            throw ( cms::CMSException ) = 0;

    };

}}

#endif /*_ACTIVEMQ_CORE_ACTIVEMQACKHANDLER_H_*/
