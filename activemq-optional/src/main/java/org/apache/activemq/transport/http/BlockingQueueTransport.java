begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ServiceStopper
import|;
end_import

begin_comment
comment|/**  * A server side HTTP based TransportChannel which processes incoming packets  * and adds outgoing packets onto a {@link Queue} so that they can be dispatched  * by the HTTP GET requests from the client.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|BlockingQueueTransport
extends|extends
name|TransportSupport
block|{
specifier|public
specifier|static
specifier|final
name|long
name|MAX_TIMEOUT
init|=
literal|30000L
decl_stmt|;
specifier|private
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|queue
decl_stmt|;
specifier|public
name|BlockingQueueTransport
parameter_list|(
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|channel
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|channel
expr_stmt|;
block|}
specifier|public
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|boolean
name|success
init|=
name|queue
operator|.
name|offer
argument_list|(
name|command
argument_list|,
name|MAX_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fail to add to BlockingQueue. Add timed out after "
operator|+
name|MAX_TIMEOUT
operator|+
literal|"ms: size="
operator|+
name|queue
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fail to add to BlockingQueue. Interrupted while waiting for space: size="
operator|+
name|queue
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
literal|"blockingQueue"
return|;
block|}
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{     }
block|}
end_class

end_unit

