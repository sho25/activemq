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
name|broker
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

begin_comment
comment|/**  * An exception thrown when the broker or transport will be shutdown in response  * to an error, eg. from IOExceptionHandler.  * The transport will die (socket.close()) so we don't want to propagate exceptions  * to the client; failover transport will retry the operation.  *  */
end_comment

begin_class
specifier|public
class|class
name|SuppressReplyException
extends|extends
name|RuntimeException
block|{
specifier|public
name|SuppressReplyException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SuppressReplyException
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SuppressReplyException
parameter_list|(
name|String
name|reason
parameter_list|,
name|IOException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|reason
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

