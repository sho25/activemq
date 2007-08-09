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
name|thread
package|;
end_package

begin_comment
comment|/**  * A Valve is a synchronization object used enable or disable the "flow" of  * concurrent processing.  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Valve
block|{
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|on
decl_stmt|;
specifier|private
name|int
name|turningOff
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|usage
init|=
literal|0
decl_stmt|;
specifier|public
name|Valve
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|this
operator|.
name|on
operator|=
name|on
expr_stmt|;
block|}
comment|/**      * Turns the valve on. This method blocks until the valve is off.      *       * @throws InterruptedException      */
specifier|public
name|void
name|turnOn
parameter_list|()
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
while|while
condition|(
name|on
condition|)
block|{
name|mutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
name|on
operator|=
literal|true
expr_stmt|;
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
name|boolean
name|isOn
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|on
return|;
block|}
block|}
comment|/**      * Turns the valve off. This method blocks until the valve is on and the      * valve is not in use.      *       * @throws InterruptedException      */
specifier|public
name|void
name|turnOff
parameter_list|()
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
try|try
block|{
operator|++
name|turningOff
expr_stmt|;
while|while
condition|(
name|usage
operator|>
literal|0
operator|||
operator|!
name|on
condition|)
block|{
name|mutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
name|on
operator|=
literal|false
expr_stmt|;
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
operator|--
name|turningOff
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Increments the use counter of the valve. This method blocks if the valve      * is off, or is being turned off.      *       * @throws InterruptedException      */
specifier|public
name|void
name|increment
parameter_list|()
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
comment|// Do we have to wait for the value to be on?
while|while
condition|(
name|turningOff
operator|>
literal|0
operator|||
operator|!
name|on
condition|)
block|{
name|mutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
name|usage
operator|++
expr_stmt|;
block|}
block|}
comment|/**      * Decrements the use counter of the valve.      */
specifier|public
name|void
name|decrement
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|usage
operator|--
expr_stmt|;
if|if
condition|(
name|turningOff
operator|>
literal|0
operator|&&
name|usage
operator|<
literal|1
condition|)
block|{
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

