begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * A useful base class for a transport implementation which has a background  * reading thread.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TransportThreadSupport
extends|extends
name|TransportSupport
implements|implements
name|Runnable
block|{
specifier|private
name|boolean
name|daemon
init|=
literal|false
decl_stmt|;
specifier|private
name|Thread
name|runner
decl_stmt|;
specifier|private
name|long
name|stackSize
init|=
literal|0
decl_stmt|;
comment|//should be a multiple of 128k
specifier|public
name|boolean
name|isDaemon
parameter_list|()
block|{
return|return
name|daemon
return|;
block|}
specifier|public
name|void
name|setDaemon
parameter_list|(
name|boolean
name|daemon
parameter_list|)
block|{
name|this
operator|.
name|daemon
operator|=
name|daemon
expr_stmt|;
block|}
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|runner
operator|=
operator|new
name|Thread
argument_list|(
literal|null
argument_list|,
name|this
argument_list|,
literal|"ActiveMQ Transport: "
operator|+
name|toString
argument_list|()
argument_list|,
name|stackSize
argument_list|)
expr_stmt|;
name|runner
operator|.
name|setDaemon
argument_list|(
name|daemon
argument_list|)
expr_stmt|;
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the stackSize      */
specifier|public
name|long
name|getStackSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|stackSize
return|;
block|}
comment|/**      * @param stackSize the stackSize to set      */
specifier|public
name|void
name|setStackSize
parameter_list|(
name|long
name|stackSize
parameter_list|)
block|{
name|this
operator|.
name|stackSize
operator|=
name|stackSize
expr_stmt|;
block|}
block|}
end_class

end_unit

