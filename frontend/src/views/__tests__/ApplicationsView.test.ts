import { flushPromises, mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import ApplicationsView from '../ApplicationsView.vue';

const { getStoredAuthToken, post, listApplications } = vi.hoisted(() => ({
  getStoredAuthToken: vi.fn(),
  post: vi.fn(),
  listApplications: vi.fn()
}));

vi.mock('../../lib/api', () => ({
  apiClient: { post },
  getStoredAuthToken,
  setAuthToken: vi.fn()
}));

vi.mock('../../api/applications', () => ({
  listApplications,
  createApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Applied' } }),
  updateApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Interview' } }),
  archiveApplication: vi.fn().mockResolvedValue({ data: { id: 1, archived: true } }),
  deleteApplication: vi.fn().mockResolvedValue({ data: { id: 1 } })
}));

describe('ApplicationsView', () => {
  let wrapper: any;

  beforeEach(() => {
    post.mockReset();
    getStoredAuthToken.mockReturnValue(null);
    listApplications.mockResolvedValue({ data: [] });
    wrapper = mount(ApplicationsView);
  });

  afterEach(() => {
    wrapper.unmount();
  });

  it('renders the form and creates an application', async () => {
    expect(wrapper.text()).toContain('Applications');

    await wrapper.get('[data-testid="application-form"]').trigger('submit.prevent');
    expect(wrapper.text()).toContain('Acme');
  });

  it('displays overdue follow-up date warning', async () => {
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 5);
    await wrapper.get('#followUpDate').setValue(pastDate.toISOString().slice(0, 10));

    // Check if the overdue warning is displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(true);
    expect(wrapper.find('.overdue-warning').text()).toBe('Overdue!');

    // Check if the input has the overdue class
    expect(wrapper.find('#followUpDate').classes()).toContain('overdue');
  });

  it('does not display overdue warning for future dates', async () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 30);
    await wrapper.get('#followUpDate').setValue(futureDate.toISOString().slice(0, 10));

    // Check that the overdue warning is NOT displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(false);

    // Check that the input does NOT have the overdue class
    expect(wrapper.find('#followUpDate').classes()).not.toContain('overdue');
  });

  it('does not display overdue warning when no date is set', async () => {
    // Ensure the follow-up date field is empty
    await wrapper.get('#followUpDate').setValue('');

    // Check that the overdue warning is NOT displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(false);
  });

  it('registers a new user and directs them to log in', async () => {
    post.mockResolvedValueOnce({ data: { message: 'User registered', data: 'ok' } });

    await wrapper.get('.auth-tabs button:nth-child(2)').trigger('click');
    await wrapper.get('[data-testid="register-form"] input[placeholder="Username"]').setValue('new-user');
    await wrapper.get('[data-testid="register-form"] input[type="password"]').setValue('password');
    await wrapper.get('[data-testid="register-form"]').trigger('submit.prevent');
    await flushPromises();

    expect(post).toHaveBeenCalledWith('/auth/register', { username: 'new-user', password: 'password' });
    expect(wrapper.find('[data-testid="login-form"]').exists()).toBe(true);
    expect(wrapper.text()).toContain('Registration successful. Please log in.');
  });

  it('shows a success message after login', async () => {
    post.mockResolvedValueOnce({ data: { data: 'login-token' } });

    await wrapper.get('[data-testid="login-form"] input[placeholder="Username"]').setValue('new-user');
    await wrapper.get('[data-testid="login-form"] input[type="password"]').setValue('password');
    await wrapper.get('[data-testid="login-form"]').trigger('submit.prevent');
    await flushPromises();

    expect(post).toHaveBeenCalledWith('/auth/login', { username: 'new-user', password: 'password' });
    expect(wrapper.find('[role="status"]').text()).toBe('Logged in successfully.');
  });

  it('shows the login loading state until the request resolves', async () => {
    let resolveLogin!: (value: { data: { data: string } }) => void;
    post.mockReturnValueOnce(new Promise((resolve) => { resolveLogin = resolve; }));

    await wrapper.get('[data-testid="login-form"]').trigger('submit.prevent');
    expect(wrapper.get('[data-testid="login-form"] button').text()).toContain('Logging in...');
    expect(wrapper.get('[data-testid="login-form"] button').attributes('disabled')).toBeDefined();
    expect(wrapper.get('[data-testid="login-form"] button span').classes()).toContain('animate-spin');

    resolveLogin({ data: { data: 'login-token' } });
    await flushPromises();

    expect(wrapper.get('[data-testid="login-form"] button').text()).toContain('Log in');
    expect(wrapper.get('[data-testid="login-form"] button').attributes('disabled')).toBeUndefined();
  });

  it('shows the register loading state until the request resolves', async () => {
    let resolveRegister!: (value: { data: { message: string; data: string } }) => void;
    post.mockReturnValueOnce(new Promise((resolve) => { resolveRegister = resolve; }));

    await wrapper.get('.auth-tabs button:nth-child(2)').trigger('click');
    await wrapper.get('[data-testid="register-form"]').trigger('submit.prevent');
    expect(wrapper.get('[data-testid="register-form"] button').text()).toContain('Registering...');
    expect(wrapper.get('[data-testid="register-form"] button').attributes('disabled')).toBeDefined();

    resolveRegister({ data: { message: 'User registered', data: 'ok' } });
    await flushPromises();

    expect(wrapper.find('[data-testid="login-form"]').exists()).toBe(true);
  });

  it('shows application loading until the initial list resolves', async () => {
    let resolveApplications!: (value: { data: never[] }) => void;
    listApplications.mockReturnValueOnce(new Promise((resolve) => { resolveApplications = resolve; }));
    getStoredAuthToken.mockReturnValue('stored-token');

    const loadingWrapper = mount(ApplicationsView);
    await loadingWrapper.vm.$nextTick();
    expect(loadingWrapper.get('[role="status"]').text()).toContain('Loading applications...');
    expect(loadingWrapper.find('.animate-spin').exists()).toBe(true);

    resolveApplications({ data: [] });
    await flushPromises();

    expect(loadingWrapper.find('[role="status"]').exists()).toBe(false);
    expect(loadingWrapper.text()).toContain('No applications yet.');
    loadingWrapper.unmount();
  });

  it('shows the duplicate username error', async () => {
    post.mockRejectedValueOnce({ response: { data: { message: 'User already exists' } } });

    await wrapper.get('.auth-tabs button:nth-child(2)').trigger('click');
    await wrapper.get('[data-testid="register-form"] input[placeholder="Username"]').setValue('existing-user');
    await wrapper.get('[data-testid="register-form"] input[type="password"]').setValue('password');
    await wrapper.get('[data-testid="register-form"]').trigger('submit.prevent');
    await flushPromises();

    expect(wrapper.find('[role="alert"]').text()).toBe('User already exists');
    expect(wrapper.find('[data-testid="register-form"]').exists()).toBe(true);
  });
});