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

begin_comment
comment|/**  * An asynchronous listener of commands  *   *   */
end_comment

begin_class
specifier|public
class|class
name|DefaultTransportListener
implements|implements
name|TransportListener
block|{
comment|/**      * called to process a command      *       * @param command      */
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{     }
comment|/**      * An unrecoverable exception has occured on the transport      *       * @param error      */
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{     }
comment|/**      * The transport has suffered an interuption from which it hopes to recover      */
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{     }
comment|/**      * The transport has resumed after an interuption      */
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{     }
block|}
end_class

end_unit

