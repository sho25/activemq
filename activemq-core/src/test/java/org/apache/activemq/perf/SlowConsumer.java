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
name|perf
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|SlowConsumer
extends|extends
name|PerfConsumer
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SlowConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|SlowConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|String
name|consumerName
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
name|consumerName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SlowConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|super
operator|.
name|onMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"GOT A MSG "
operator|+
name|msg
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

