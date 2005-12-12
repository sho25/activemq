begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_class
specifier|public
class|class
name|QueueSystemTest
extends|extends
name|SystemTestSupport
block|{
comment|/**      * Unit test for persistent queue messages with the following settings:      * 1 Producer, 1 Consumer, 1 Subject, 10 Messages      *      * @throws Exception      */
comment|///*
specifier|public
name|void
name|testPersistentQueueMessageA
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testPersistentQueueMessageA()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for persistent queue messages with the following settings:      * 10 Producers, 10 Consumers, 1 Subject, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testPersistentQueueMessageB
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testPersistentQueueMessageB()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for persistent queue messages with the following settings:      * 10 Producers, 10 Consumers, 10 Subjects, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testPersistentQueueMessageC
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|"testPersistentQueueMessageC()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for non-persistent queue messages with the following settings:      * 1 Producer, 1 Consumer, 1 Subject, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentQueueMessageA
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentQueueMessageA()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for non-persistent queue messages with the following settings:      * 10 Producers, 10 Consumers, 1 Subject, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentQueueMessageB
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentQueueMessageB()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Unit test for non-persistent queue messages with the following settings:      * 10 Producers, 10 Consumers, 10 Subjects, 10 Messages      *      * @throws Exception      */
specifier|public
name|void
name|testNonPersistentQueueMessageC
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemTestSupport
name|st
init|=
operator|new
name|SystemTestSupport
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|"testNonPersistentQueueMessageC()"
argument_list|)
decl_stmt|;
name|st
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

