begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|management
package|;
end_package

begin_class
specifier|public
class|class
name|CountStatisticTest
extends|extends
name|StatisticTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CountStatisticTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Use case for CountStatisticImple class.      * @throws Exception      */
specifier|public
name|void
name|testStatistic
parameter_list|()
throws|throws
name|Exception
block|{
name|CountStatisticImpl
name|stat
init|=
operator|new
name|CountStatisticImpl
argument_list|(
literal|"myCounter"
argument_list|,
literal|"seconds"
argument_list|,
literal|"myDescription"
argument_list|)
decl_stmt|;
name|assertStatistic
argument_list|(
name|stat
argument_list|,
literal|"myCounter"
argument_list|,
literal|"seconds"
argument_list|,
literal|"myDescription"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stat
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|increment
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|increment
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stat
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|stat
operator|.
name|decrement
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stat
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|stat
operator|.
name|increment
argument_list|()
expr_stmt|;
name|assertLastTimeNotStartTime
argument_list|(
name|stat
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Counter is: "
operator|+
name|stat
argument_list|)
expr_stmt|;
name|stat
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stat
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

