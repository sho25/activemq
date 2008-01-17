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
name|logwriters
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|LogWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  * Implementation of LogWriter interface to keep ActiveMQ's  * old logging format.  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DefaultLogWriter
implements|implements
name|LogWriter
block|{
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|initialMessage
parameter_list|(
name|Log
name|log
parameter_list|)
block|{
comment|// Default log writer does nothing here
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logRequest
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logResponse
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|response
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"GOT RESPONSE: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logAsyncRequest
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING ASNYC REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logOneWay
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logReceivedCommand
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"RECEIVED: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
comment|// doc comment inherited from LogWriter
specifier|public
name|void
name|logReceivedException
parameter_list|(
name|Log
name|log
parameter_list|,
name|IOException
name|error
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"RECEIVED Exception: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

