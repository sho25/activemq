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
name|ConcurrentLinkedQueue
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
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|StubTransport
extends|extends
name|TransportSupport
block|{
specifier|private
name|Queue
name|queue
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|()
decl_stmt|;
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
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{     }
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
name|queue
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Queue
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

