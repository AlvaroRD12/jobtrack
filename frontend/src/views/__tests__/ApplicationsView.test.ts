import { flushPromises, mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import ApplicationsView from '../ApplicationsView.vue';

const { post } = vi.hoisted(() => ({ post: vi.fn() }));

vi.mock('../../lib/api', () => ({
  apiClient: { post },
  getStoredAuthToken: vi.fn().mockReturnValue(null),
  setAuthToken: vi.fn()
}));

vi.mock('../../api/applications', () => ({
  listApplications: vi.fn().mockResolvedValue({ data: [] }),
  createApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Applied' } }),
  updateApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Interview' } }),
  archiveApplication: vi.fn().mockResolvedValue({ data: { id: 1, archived: true } }),
  deleteApplication: vi.fn().mockResolvedValue({ data: { id: 1 } })
}));

describe('ApplicationsView', () => {
  let wrapper: any;

  beforeEach(() => {
    post.mockReset();
    wrapper = mount(ApplicationsView);
  });

  it('renders the form and creates an application', async () => {
    expect(wrapper.text()).toContain('Applications');

    await wrapper.get('[data-testid="application-form"]').trigger('submit.prevent');
    expect(wrapper.text()).toContain('Acme');
  });

  it('displays overdue follow-up date warning', async () => {
    // Set a past date in the follow-up field
    const pastDate = '2026-01-01'; // Definitely in the past
    await wrapper.get('#followUpDate').setValue(pastDate);

    // Check if the overdue warning is displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(true);
    expect(wrapper.find('.overdue-warning').text()).toBe('Overdue!');

    // Check if the input has the overdue class
    expect(wrapper.find('#followUpDate').classes()).toContain('overdue');
  });

  it('does not display overdue warning for future dates', async () => {
    // Set a future date in the follow-up field
    const futureDate = '2026-12-31'; // Definitely in the future
    await wrapper.get('#followUpDate').setValue(futureDate);

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