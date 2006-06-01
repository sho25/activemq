begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_class
specifier|public
class|class
name|PerfEventAdapter
implements|implements
name|PerfEventListener
block|{
specifier|public
name|void
name|onConfigStart
parameter_list|()
block|{     }
specifier|public
name|void
name|onConfigEnd
parameter_list|()
block|{     }
specifier|public
name|void
name|onPublishStart
parameter_list|()
block|{     }
specifier|public
name|void
name|onPublishEnd
parameter_list|()
block|{     }
specifier|public
name|void
name|onConsumeStart
parameter_list|()
block|{     }
specifier|public
name|void
name|onConsumeEnd
parameter_list|()
block|{     }
specifier|public
name|void
name|onJMSException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{     }
specifier|public
name|void
name|onException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
end_class

end_unit

