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
#ifndef JournalTrace_hpp_
#define JournalTrace_hpp_

#include <string>
#include "command/AbstractCommand.hpp"
    

#include "util/ifr/ap"
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
        using namespace apache::activemq::client;

/*
 *
 *  Marshalling code for Open Wire Format for JournalTrace
 *
 *
 *  NOTE!: This file is autogenerated - do not modify!
 *         if you need to make a change, please see the Groovy scripts in the
 *         activemq-core module
 *
 */
class JournalTrace : public AbstractCommand
{
private:
    p<string> message ;

public:
    const static int TYPE = 53;

public:
    JournalTrace() ;
    virtual ~JournalTrace() ;

    virtual int getCommandType() ;

    virtual p<string> getMessage() ;
    virtual void setMessage(p<string> message) ;


} ;

/* namespace */
      }
    }
  }
}

#endif /*JournalTrace_hpp_*/
