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
#ifndef ProducerId_hpp_
#define ProducerId_hpp_

#include <string>
#include "command/AbstractCommand.hpp"
#include "util/ifr/p"

namespace apache
{
  namespace activemq
  {
    namespace client
    {
      namespace command
      {
        using namespace ifr;
        using namespace std;

/*
 * Dummy, should be auto-generated.
 */
class ProducerId : public AbstractCommand
{
private:
    p<string> connectionId ;
    long      sessionId,
              producerId ;

    const static int TYPE = 123 ;

public:
    ProducerId() ;
    virtual ~ProducerId() ;

    virtual int getCommandType() ;
    virtual void setValue(long producerId) ;
    virtual long getValue() ;
    virtual void setSessionId(long sessionId) ;
    virtual long getSessionId() ;
    virtual void setConnectionId(const char* connectionId) ;
    virtual p<string> getConnectionId() ;
} ;

/* namespace */
      }
    }
  }
}

#endif /*ProducerId_hpp_*/
